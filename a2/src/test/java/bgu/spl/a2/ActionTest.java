package bgu.spl.a2;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.CountDownLatch;

@RunWith(Parameterized.class)
public class ActionTest {

	@Parameterized.Parameters
	public static Collection parameters() {
		Object[][] params = new Object[1000][];
		for (int i = 0; i < params.length; i++) {
			params[i] = new Object[]{i % 10 + 1};
		}
		return Arrays.asList(params);
	}

	public ActionTest(int nThreads) {
		this.nThreads = nThreads;
	}

	private int nThreads;
	private ActorThreadPool threadPool;

	@Before
	public void setUp() {
		threadPool = new ActorThreadPool(nThreads);
	}

	@Test
	public void transactionTest() {
		Transaction transaction = new Transaction(100, "A", "B", "Bank2");
		threadPool.start();
		threadPool.submit(new AddClient("A", 100, "Bank1"), "Bank1", new BankState());
		threadPool.submit(new AddClient("B", 0, "Bank2"), "Bank2", new BankState());
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		threadPool.submit(transaction, "Bank1", new BankState());
		try {
			CountDownLatch latch = new CountDownLatch(1);
			transaction.getResult().subscribe(() -> {
				System.out.println(threadPool);
				latch.countDown();
			});
			latch.await();
			threadPool.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class AddClient extends Action<Boolean> {
		private String name;
		private int amount;
		private String bank;

		public AddClient(String name, int amount, String bank) {
			System.out.println();
			this.name = name;
			this.amount = amount;
			this.bank = bank;
			setActionName("AddClient " + name);
		}

		@Override
		protected void start() {
			System.out.println("#### " + getActionName() + ": start()");
			((BankState) state).clients.add(new Pair<>(name, amount));
			System.out.println("added client " + name + " to bank " + bank);
			complete(true);
		}
	}

	private class Transaction extends Action<String> {
		int amount;
		String sender;
		String receiver;
		String receiverBank;

		public Transaction(int amount, String sender, String receiver, String receiverBank) {
			this.amount = amount;
			this.sender = sender;
			this.receiver = receiver;
			this.receiverBank = receiverBank;
			setActionName("Transaction");
		}

		@Override
		protected void start() {
			System.out.println("#### " + getActionName() + ": start()");
			Action<Boolean> confAction = new Confirmation(amount, sender, receiver, receiverBank);
			sendMessage(confAction, receiverBank, new BankState());
			then(Collections.singletonList(confAction), () -> {
				if (confAction.getResult().get()) {
					System.out.println("Transaction Succeeded");
					Pair<String, Integer> client = ((BankState) state).clients.remove(0);
					((BankState) state).clients.add(new Pair<>(client.getKey(), client.getValue() - amount));
					complete("Transaction Succeeded");
				} else {
					System.out.println("Transaction Failed");
					complete("Transaction Failed");
				}
			});
		}
	}

	private class Confirmation extends Action<Boolean> {
		int amount;
		String sender;
		String receiver;
		String receiverBank;

		public Confirmation(int amount, String sender, String receiver, String receiverBank) {
			this.amount = amount;
			this.sender = sender;
			this.receiver = receiver;
			this.receiverBank = receiverBank;
			setActionName("Confirmation");
		}

		@Override
		protected void start() {
			System.out.println("#### " + getActionName() + ": start()");
			if (Math.random() < 0.5) {
				Pair<String, Integer> client = ((BankState) state).clients.remove(0);
				((BankState) state).clients.add(new Pair<>(client.getKey(), client.getValue() + amount));
				System.out.println(receiverBank + " confirmed transaction from: " + sender + " to " + receiver);
				complete(true);
			} else {
				System.out.println(receiverBank + " unconfirmed transaction from: " + sender + " to " + receiver);
				complete(false);
			}
		}
	}

	private class BankState extends PrivateState {
		List<Pair<String, Integer>> clients = new ArrayList<>();

		@Override
		public String toString() {
			return clients.get(0).getKey() + ": " + clients.get(0).getValue();
		}
	}
}