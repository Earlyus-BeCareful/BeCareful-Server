package com.becareful.becarefulserver.domain.sms.repository;

import org.springframework.data.repository.CrudRepository;

import com.becareful.becarefulserver.domain.sms.domain.SmsAuthentication;

public interface SmsRepository extends CrudRepository<SmsAuthentication, String> {}
