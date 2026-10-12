package dev.ioana.lessons_of_wellbeing.enums;

public enum Drink {
    ROYAL_CHAMOMILE_LAVENDER("Queen Mia's Royal Chamomile Lavender"),
    SPARK_GINGER_CITRUS_KOMBUCHA("Thor's Spark Ginger Citrus Kombucha"),
    GOLDEN_MILK_CARDAMOM_CHAI("Feline Golden Milk & Cardamom Chai"),
    HIBISCUS_ROSE_SPARKLE_KOMBUCHA("Hibiscus Rose Sparkle Kombucha");

    private final String displayName;

    Drink(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
