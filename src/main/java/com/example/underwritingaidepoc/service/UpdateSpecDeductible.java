package com.example.underwritingaidepoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateSpecDeductible {

    private final SpecDeductibleService specDeductibleService;

    @Autowired
    public UpdateSpecDeductible(SpecDeductibleService specDeductibleService) {
        this.specDeductibleService = specDeductibleService;
    }

    public InputDetails updateSpecDeductible(Quote quote, InputDetails inputDetails) {
        SpecDeductible suggestedSpecDeductible = specDeductibleService.calculateSpecDeductible(quote, inputDetails);
        SpecDeductible specDeductible = inputDetails.getDetails().getSpecDeductible();
        inputDetails.getDetails().setSpecDeductible(suggestedSpecDeductible);
        return inputDetails;
    }
}
