package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class Experiment {

	public static void main(String[] args) {
		if(args.length!=2){
			System.out.println("Params: <broker class> <workload file>");
			System.exit(1);
		}
		
		String brokerClass = args[0];
		String workloadFile = args[1];
		
		printSimulationProperties(brokerClass,workloadFile);

		int rounds = Integer.parseInt(ExperimentProperties.EXPERIMENT_ROUNDS.getProperty());

		for (int round = 1; round <= rounds; round++) {
			runSimulationRound(round, brokerClass, workloadFile);
		}
	}

	private static void printSimulationProperties(String brokerClass, String workloadFile) {
		Log.printLine("========== Experiment configuration ==========");
		Log.printLine("= brokerClass: "+brokerClass);
		Log.printLine("= workloadFile: "+workloadFile);
		for (ExperimentProperties property: ExperimentProperties.values()){
			Log.printLine("= "+property+": "+property.getProperty());
		}
		Log.printLine("==============================================");
		Log.printLine("");
	}

	private static void runSimulationRound(int round, String brokerClass, String workloadFile) {
		Log.printLine("Starting InterCloud experiment round " + round + "...");

		try {
			int num_user = 1;
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;

			CloudSim.init(num_user, calendar, trace_flag);
			
			int localHosts = Integer.parseInt(ExperimentProperties.HOSTS_PERDATACENTER.getProperty());

			Datacenter datacenter = createDatacenter("Local",localHosts);
			Datacenter cloud = createDatacenter("Cloud",500);
			Broker broker = createBroker(brokerClass,workloadFile);

			// NETWORK PROPERTIES
			double latency = Double.parseDouble(ExperimentProperties.NETWORK_LATENCY.getProperty());
			double bw = Double.parseDouble(ExperimentProperties.NETWORK_BANDWIDTH.getProperty());
			
			//higher latency between cloud and broker
			NetworkTopology.addLink(cloud.getId(), broker.getId(), bw, latency);
			
			//low latency between broker and local resources
			NetworkTopology.addLink(datacenter.getId(), broker.getId(), 10000, 0.1);

			CloudSim.startSimulation();
			broker.printExecutionSummary();

			Log.printLine("Experiment finished!");
			Log.printLine("");
			Log.printLine("");
		} catch (Exception e) {
			Log.printLine("Unwanted errors happen.");
			e.printStackTrace();
		}
	}

	private static Datacenter createDatacenter(String name, int hosts) throws Exception {

		// HOST PROPERTIES
		int ram = Integer.parseInt(ExperimentProperties.MEMORY_PERHOST.getProperty());
		int cores = Integer.parseInt(ExperimentProperties.CORES_PERHOST.getProperty());
		int mips = Integer.parseInt(ExperimentProperties.MIPS_PERCORE.getProperty());
		long storage = Long.parseLong(ExperimentProperties.STORAGE_PERHOST.getProperty());
		int bw = Integer.parseInt(ExperimentProperties.BANDWIDTH_PERHOST.getProperty());

		List<Host> hostList = new ArrayList<Host>();
		for (int i = 0; i < hosts; i++) {
			List<Pe> peList = new ArrayList<Pe>();
			for (int j = 0; j < cores; j++) {
				peList.add(new Pe(j, new PeProvisionerSimple(mips)));
			}

			hostList.add(new Host(
					i,
					new RamProvisionerSimple(ram),
					new BwProvisionerSimple(bw),
					storage,
					peList,
					new VmSchedulerSpaceShared(peList)));
		}

		String arch = "Xeon";
		String os = "Linux";
		String vmm = "Xen";
		double time_zone = 10.0;
		double cost = 0.0;
		double costPerMem = 0.00;
		double costPerStorage = 0.0;
		double costPerBw = 0.0;
		LinkedList<Storage> storageList = new LinkedList<Storage>();

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch,
				os,
				vmm,
				hostList,
				time_zone,
				cost,
				costPerMem,
				costPerStorage,
				costPerBw);

		return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0.1);
	}
	
	private static Broker createBroker(String className, String workloadFile) {
		
		Broker broker = null;
		
		try {
		
			int initialVms = Integer.parseInt(ExperimentProperties.LOCAL_VMS.getProperty());
			
			//Class<?> brokerClass = Class.forName(className,true,WorstFitJobDpBroker.class.getClassLoader());
			//Constructor<?> ctor = brokerClass.getConstructor(String.class, String.class, int.class);
			//ctor.setAccessible(true);
			//broker = (Broker) ctor.newInstance(brokerClass.getName(),workloadFile,initialVms);
			
			if (className.equals("RoundRobinTaskDpBroker")){
				broker = new RoundRobinTaskDpBroker("RoundRobinTaskDpBroker",workloadFile,initialVms);
			} else if (className.equals("RoundRobinJobDpBroker")){
				broker = new RoundRobinJobDpBroker("RoundRobinJobDpBroker",workloadFile,initialVms);
			} else if (className.equals("HeftTaskDpBroker")){
				broker = new HeftTaskDpBroker("WorstFitTaskDpBroker",workloadFile,initialVms);
			} else if (className.equals("HeftJobDpBroker")){
				broker = new HeftJobDpBroker("WorstFitJobDpBroker",workloadFile,initialVms);
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return broker;
	}
}