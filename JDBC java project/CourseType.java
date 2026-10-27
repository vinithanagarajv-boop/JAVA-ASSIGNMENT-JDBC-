/**
 * Kongu Engineering College (Autonomous) - Regulation 2024
 * CourseType Enum defining weightage and evaluation thresholds for
 * THEORY, PRACTICAL, and EMBEDDED courses.
 */
public enum CourseType {
    THEORY(0.40, 0.60, 45.0, 50.0),
    PRACTICAL(0.60, 0.40, 50.0, 50.0),
    EMBEDDED(0.50, 0.50, 45.0, 50.0);

    private final double ciaWeightage;            // e.g. 0.40 (40%)
    private final double eseWeightage;            // e.g. 0.60 (60%)
    private final double eseMinThresholdPercent;  // e.g. 45% of ESE (raw 45/100)
    private final double passMinimumTotal;        // e.g. 50 Marks

    CourseType(double ciaWeightage, double eseWeightage, double eseMinThresholdPercent, double passMinimumTotal) {
        this.ciaWeightage = ciaWeightage;
        this.eseWeightage = eseWeightage;
        this.eseMinThresholdPercent = eseMinThresholdPercent;
        this.passMinimumTotal = passMinimumTotal;
    }

    public double getCiaWeightage() {
        return ciaWeightage;
    }

    public double getEseWeightage() {
        return eseWeightage;
    }

    public double getEseMinThresholdPercent() {
        return eseMinThresholdPercent;
    }

    public double getPassMinimumTotal() {
        return passMinimumTotal;
    }

    /**
     * Minimum scaled mark needed in ESE to pass.
     * THEORY: 60 * 45% = 27.0
     * PRACTICAL: 40 * 50% = 20.0
     * EMBEDDED: 50 * 45% = 22.5
     */
    public double getEseMinScaledScore() {
        return eseWeightage * eseMinThresholdPercent;
    }

    public static CourseType fromString(String text) {
        if (text == null) return THEORY;
        for (CourseType t : CourseType.values()) {
            if (t.name().equalsIgnoreCase(text.trim())) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown Course Type: " + text + ". Must be THEORY, PRACTICAL, or EMBEDDED.");
    }
}
