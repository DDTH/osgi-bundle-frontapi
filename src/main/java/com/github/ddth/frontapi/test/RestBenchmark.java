package com.github.ddth.frontapi.test;

import java.util.HashMap;
import java.util.Map;

import com.github.ddth.frontapi.ApiResult;
import com.github.ddth.frontapi.client.RestApiClient;
import com.github.ddth.frontapi.internal.Activator;

public class RestBenchmark {

	public static void main(String[] args) throws Exception {
		final int NUM_THREADS = 32;
		final int TOTAL_RUN = 100000;

		final RestApiClient[] CLIENTS = new RestApiClient[NUM_THREADS];
		for (int i = 0; i < NUM_THREADS; i++) {
			CLIENTS[i] = new RestApiClient("http://localhost:9000/api");
		}
		final Map<String, Object> data = new HashMap<String, Object>();
		for (int i = 0; i < 4; i++) {
			int total_run = i == 0 ? 1000 : TOTAL_RUN;
			int num_threads = i == 0 ? 1 : NUM_THREADS;
			BenchmarkResult result = new Benchmark(new Operation() {
				@SuppressWarnings("unused")
				@Override
				public void run(int runId) {
					try {
						RestApiClient client = CLIENTS[runId % NUM_THREADS];
						ApiResult apiResult = client.call("-",
								Activator.MODULE_NAME, "ping", data);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}, total_run, num_threads).run();
			System.out.println(result.summarize());
		}
	}

}
