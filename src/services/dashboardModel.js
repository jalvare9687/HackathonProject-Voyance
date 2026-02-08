export function buildDashboardFromHealthRows(healthRows) {
  // Defensive defaults
  const rows = Array.isArray(healthRows) ? healthRows : [];

  const statusLower = (s) => String(s || "").toLowerCase();

  // Count items that are NOT OK (for "Critical Items")
  const criticalCount = rows.filter((r) => {
    const s = statusLower(r.status);
    return s !== "ok" && s !== "none" && s !== "";
  }).length;

  // Avg days-until-stockout
  const avgDays =
    rows.length
      ? rows.reduce((sum, r) => sum + (Number(r.daysUntilStockout) || 0), 0) / rows.length
      : 0;

  // Total "on hand" (sum of currentStock)
  const totalStock = rows.reduce((sum, r) => sum + (Number(r.currentStock) || 0), 0);

  // Total daily burn
  const totalBurn = rows.reduce((sum, r) => sum + (Number(r.dailyBurnRate) || 0), 0);

  // Table rows (map to what UI wants)
  const stockouts = rows
    .slice()
    .sort((a, b) => (Number(a.daysUntilStockout) || 9999) - (Number(b.daysUntilStockout) || 9999))
    .map((r, idx) => {
      const days = Number(r.daysUntilStockout) || 0;
      const status = statusLower(r.status);

      let label = "Normal";
      if (status.includes("critical") || days <= 2) label = "Critical";
      else if (status.includes("warning") || days <= 5) label = "Warning";

      return {
        id: `ING-${String(idx + 1).padStart(4, "0")}`,
        item: r.ingredientName || "Unknown",
        daysRemaining: days,
        currentStock: Number(r.currentStock) || 0,
        dailyBurnRate: Number(r.dailyBurnRate) || 0,
        recommendation: r.recommendation || "None",
        status: label,
        // --- ADD THESE TWO LINES ---
        markup: r.suggestedPriceMarkup || 0,
        rationale: r.pricingRationale || "Standard Pricing"
      };
    });

  return {
    kpis: { totalStock, totalBurn, criticalCount, avgDays },
    stockouts,
  };
}
