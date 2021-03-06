package com.db.awmd.challenge.web;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.FromToAccountSameException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.service.TransfersService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/transfers")
@Slf4j
public class TransfersController {

	private final TransfersService transfersService;

	@Autowired
	public TransfersController(TransfersService transfersService) {
		this.transfersService = transfersService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> transferMoney(@RequestBody @Valid Transfer transfer) {
		log.info("Transfering {} from account {} to {}", transfer.getAmount(), transfer.getAccountFromId(),
				transfer.getAccountToId());

		try {
			this.transfersService.transferMoney(transfer);
		} catch (InsufficientBalanceException ibe) {
			return new ResponseEntity<>(ibe.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (FromToAccountSameException ftase) {
			return new ResponseEntity<>(ftase.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (AccountNotFoundException anfe) {
			return new ResponseEntity<>(anfe.getMessage(), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("Amount transfered.", HttpStatus.CREATED);
	}
}
