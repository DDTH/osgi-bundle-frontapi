package com.github.ddth.frontapi.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.github.ddth.frontapi.ApiResult;
import com.github.ddth.frontapi.client.IApiClient;
import com.github.ddth.frontapi.client.ThriftApiClient;
import com.github.ddth.frontapi.internal.Activator;

public class PooledThriftBenchmark {

	static class ApiClientFactory extends BasePoolableObjectFactory<IApiClient> {
		@Override
		public IApiClient makeObject() throws Exception {
			ThriftApiClient apiClient = new ThriftApiClient("localhost", 9090);
			apiClient.init();
			return apiClient;
		}

		@Override
		public void destroyObject(IApiClient apiClient) {
			((ThriftApiClient) apiClient).destroy();
		}
	}

	public static void main(String[] args) throws Exception {
		final int NUM_THREADS = 128;
		final int TOTAL_RUN = 1000000;

		final ObjectPool<IApiClient> pool = new GenericObjectPool<IApiClient>(
				new ApiClientFactory(), NUM_THREADS * 2);
		final Map<String, Object> data = new HashMap<String, Object>();
		for (int i = 0; i < 4; i++) {
			int total_run = i == 0 ? 1000 : TOTAL_RUN;
			int num_threads = i == 0 ? 1 : NUM_THREADS;
			BenchmarkResult result = new Benchmark(new Operation() {
				@SuppressWarnings("unused")
				@Override
				public void run(int runId) {
					try {
						IApiClient client = pool.borrowObject();
						try {
							ApiResult apiResult = client.call("-",
									Activator.MODULE_NAME, "ping", data);
						} finally {
							pool.returnObject(client);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}, total_run, num_threads).run();
			System.out.println(result.summarize());
		}
	}

}
