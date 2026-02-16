public class StandardBillingStrategy implements BillingStrategy {
    @Override
    public int apply(int baseBill, int mechanicExperienceYears) {
        return baseBill;
    }
}
