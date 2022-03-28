package org.cloudbus.cloudsim;


public enum ExperimentProperties {
	EXPERIMENT_ROUNDS("experiment.rounds"),
	LOCAL_VMS("experiment.localvms"),
	HOSTS_PERDATACENTER("datacenter.hosts"),
	CORES_PERHOST("host.cores"),
	MEMORY_PERHOST("host.memory"),
	STORAGE_PERHOST("host.storage"),
	BANDWIDTH_PERHOST("host.bandwidth"),
	VM_CREATION_DELAY("vm.creation.delay"),
	MIPS_PERCORE("core.mips"),
	NETWORK_LATENCY("network.latency"),
	NETWORK_BANDWIDTH("network.bandwidth"),
	BROKER_REGULARSHARE("broker.regular.share"),
	BROKER_USERS("broker.users"),
	BROKER_ESTIMATIONERROR("broker.estimation.error"),
	BROKER_MAXVMSPERUSER("broker.vms.max.peruser");

	private String key;
	private final ExperimentConfiguration configuration = ExperimentConfiguration.INSTANCE;

	ExperimentProperties(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}

	public String getProperty() {
		return configuration.getProperty(this.key);
	}

	public void setProperty(String value) {
		configuration.setProperty(this.key, value);
	}
}
