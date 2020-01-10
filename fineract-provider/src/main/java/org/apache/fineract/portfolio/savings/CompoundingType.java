package org.apache.fineract.portfolio.savings;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

public enum CompoundingType {
	INVALID(0, "CompoundingType.invalid"), //
    COMPOUNDING(1, "CompoundingType.compounding"), //
    NON_COMPOUNDING(2, "CompoundingType.non.compounding");

    private final Integer value;
    private final String code;

    private CompoundingType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }
    
    public static Object[] validValues() {
        return new Object[] { CompoundingType.COMPOUNDING.getValue(), CompoundingType.NON_COMPOUNDING.getValue()};
    }
    
    public static CompoundingType fromInt(final Integer compoundingType) {
        CompoundingType compoundingPeriodType = CompoundingType.INVALID;
            switch (compoundingType) {
                case 1:
                	compoundingPeriodType = COMPOUNDING;
                break;
                case 2:
                	compoundingPeriodType = NON_COMPOUNDING;
                break;
            }
        return compoundingPeriodType;
    }

	public static EnumOptionData compoundingType(CompoundingType type) {
		EnumOptionData optionData = new EnumOptionData(CompoundingType.INVALID.getValue().longValue(),
				CompoundingType.INVALID.getCode(), "Invalid");
        switch (type) {
            case INVALID:
            break;
            case COMPOUNDING:
                optionData = new EnumOptionData(CompoundingType.COMPOUNDING.getValue().longValue(),
                		CompoundingType.COMPOUNDING.getCode(), "Compounding");
            break;
            case NON_COMPOUNDING:
            	optionData = new EnumOptionData(CompoundingType.NON_COMPOUNDING.getValue().longValue(),
            			CompoundingType.NON_COMPOUNDING.getCode(), "Non Compounding");
            break;

        }
        return optionData;
	}
    
    public boolean Compounding() {
        return this.value.equals(CompoundingType.COMPOUNDING.getValue());
    }

    public boolean nonCompounding() {
        return this.value.equals(CompoundingType.NON_COMPOUNDING.getValue());
    }
    


}
