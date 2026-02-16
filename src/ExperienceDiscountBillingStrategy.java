public class ExperienceDiscountBillingStrategy implements BillingStrategy {
    @Override
    public int apply(int baseBill, int mechanicExperienceYears) {
        if (mechanicExperienceYears >= 15) {
            return (int) Math.round(baseBill * 0.9);
        }
        if (mechanicExperienceYears >= 8) {
            return (int) Math.round(baseBill * 0.95);
        }
        return baseBill;
    }
}
