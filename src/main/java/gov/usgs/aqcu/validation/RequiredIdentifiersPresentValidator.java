package gov.usgs.aqcu.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import gov.usgs.aqcu.parameter.VDiagramRequestParameters;

public class RequiredIdentifiersPresentValidator implements ConstraintValidator<RequiredIdentifiersPresent, VDiagramRequestParameters> {

	@Override
	public void initialize(RequiredIdentifiersPresent constraintAnnotation) {
		// Nothing to see here.
	}

	@Override
	public boolean isValid(VDiagramRequestParameters value, ConstraintValidatorContext context) {
		return !(value.getPrimaryTimeseriesIdentifier() == null 
				&& value.getUpchainTimeseriesIdentifier() == null);
	}

}
