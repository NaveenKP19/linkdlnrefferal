package com.personalproject.linkdlnrefferal.repository;

import com.personalproject.linkdlnrefferal.entity.Opportunity;
import org.springframework.stereotype.Repository;

@Repository
public interface OpportunityRepository extends OwnedEntityRepository<Opportunity> {
}
