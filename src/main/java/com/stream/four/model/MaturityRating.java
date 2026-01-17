package com.stream.four.model;

public enum MaturityRating
{
    ALL(0),
    PG(7),
    TEEN(13),
    MATURE(18);

    private final int minAge;

    MaturityRating(int minAge) {
        this.minAge = minAge;
    }

    public int getMinAge() {
        return minAge; // 18+
    }
}