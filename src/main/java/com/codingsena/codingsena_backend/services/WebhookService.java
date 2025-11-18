package com.codingsena.codingsena_backend.services;

import com.stripe.model.Event;

public interface WebhookService {
	void processEvent(Event event);
}
