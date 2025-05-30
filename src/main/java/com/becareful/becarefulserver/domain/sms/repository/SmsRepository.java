package com.becareful.becarefulserver.domain.sms.repository;

import com.becareful.becarefulserver.domain.sms.domain.SmsAuthentication;
import org.springframework.data.repository.CrudRepository;

public interface SmsRepository extends CrudRepository<SmsAuthentication, String> {}
