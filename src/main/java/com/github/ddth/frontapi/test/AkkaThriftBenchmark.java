package com.github.ddth.frontapi.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.github.ddth.frontapi.ApiResult;
import com.github.ddth.frontapi.client.ThriftApiClient;
import com.github.ddth.frontapi.internal.Activator;

public class AkkaThriftBenchmark {

	static ThriftApiClient apiClient;
	static AtomicLong COUNTER = new AtomicLong(0);

	static class ApiClientActor extends UntypedActor {
		public void onReceive(Object message) throws Exception {
			synchronized (apiClient) {
				ApiResult apiResult = apiClient.call("-",
						Activator.MODULE_NAME, "ping", message);
				COUNTER.incrementAndGet();
			}
		}
	}

	static class GuardThread extends Thread {

		private long guardValue;

		public GuardThread(long guardValue) {
			this.guardValue = guardValue;
		}

		public void run() {
			while (COUNTER.get() < guardValue) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		final int NUM_THREADS = 8;
		final int TOTAL_RUN = 10000;

		apiClient = new ThriftApiClient("localhost", 9090);
		apiClient.init();

		final ActorSystem system = ActorSystem.create("MySystem");
		final ActorRef[] ACTORS = new ActorRef[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			ACTORS[i] = system.actorOf(Props.create(ApiClientActor.class),
					"myactor" + i);
		}

		GuardThread guardThread = new GuardThread(TOTAL_RUN * 3 + 100);
		guardThread.start();
		final Map<String, Object> data = new HashMap<String, Object>();
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < 4; i++) {
			int total_run = i == 0 ? 100 : TOTAL_RUN;
			int num_threads = i == 0 ? 1 : NUM_THREADS;
			BenchmarkResult result = new Benchmark(new Operation() {
				@Override
				public void run(int runId) {
					ActorRef clientActor = ACTORS[runId % NUM_THREADS];
					clientActor.tell(data, ActorRef.noSender());
				}

			}, total_run, num_threads).run();
			System.out.println(result.summarize());
		}
		guardThread.join();
		long t2 = System.currentTimeMillis();
		long value = COUNTER.get();
		double rate = 1000.0 * value / (t2 - t1);
		System.out.println("Run [" + value + "] times in [" + (t2 - t1)
				+ "] ms: " + rate + " ops/sec.");
		System.out.println(COUNTER.get());

		system.shutdown();
		system.awaitTermination();
	}
}
