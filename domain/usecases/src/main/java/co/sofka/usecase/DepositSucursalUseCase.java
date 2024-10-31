package co.sofka.usecase;



import co.sofka.Account;
import co.sofka.Bank;
import co.sofka.BankTransaction;
import co.sofka.TypeTransaction;
import co.sofka.dto.*;
import co.sofka.gateway.IAccountRepository;
import co.sofka.gateway.IBankRepository;
import co.sofka.gateway.IBankTransactionRepository;
import co.sofka.middleware.AccountNotExistException;
import co.sofka.middleware.AccountNotHaveBalanceException;
import co.sofka.middleware.TypeOfBuysNotExistException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

public class DepositSucursalUseCase  {

    private static final Logger logger = LoggerFactory.getLogger(DepositSucursalUseCase.class);

    private final IBankTransactionRepository repository;

    private final IAccountRepository accountRepository;

    private final IBankRepository bankRepository;

    public DepositSucursalUseCase(IBankTransactionRepository repository, IAccountRepository accountRepository, IBankRepository bankRepository) {
        this.repository = repository;
        this.accountRepository = accountRepository;
        this.bankRepository = bankRepository;
    }

    @Transactional
    public BankTransaction depositSucursal(BankTransactionDepositSucursal item) {

        Account client = accountRepository.findByNumber(item.getAccountNumberClient());
        if (client == null) {
            throw new AccountNotExistException("La cuenta del store no existe");
        }

        client.setBalance(client.getBalance() + (item.getAmount()));
        accountRepository.save(client);

        BankTransaction bankTransaction1 = new BankTransaction();
        bankTransaction1.setOriginAccount(client);
        bankTransaction1.setDestinationAccount(client);
        bankTransaction1.setAmount(item.getAmount());

        TypeTransaction typeTransaction = new TypeTransaction();
        typeTransaction.setId("1");

        bankTransaction1.setTypeTransaction(typeTransaction);
        bankTransaction1.setCreatedAt(new Date(System.currentTimeMillis()));
        return repository.save(bankTransaction1);
    }
}
