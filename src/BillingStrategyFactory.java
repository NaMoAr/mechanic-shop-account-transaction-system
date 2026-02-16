public class BillingStrategyFactory {
    public BillingStrategy create(int mechanicExperienceYears) {
        if (mechanicExperienceYears >= 8) {
            return new ExperienceDiscountBillingStrategy();
        }
        return new StandardBillingStrategy();
    }
}
