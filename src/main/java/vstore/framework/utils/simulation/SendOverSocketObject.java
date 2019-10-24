package vstore.framework.utils.simulation;

import java.io.Serializable;
import java.util.List;

import vstore.framework.file.VStoreFile;
import vstore.framework.node.NodeInfo;

public class SendOverSocketObject implements Serializable {
	
	VStoreFile file;
	List<NodeInfo> nodeList;
	String phoneId;
	
	public SendOverSocketObject(VStoreFile f, List<NodeInfo> nodes, String phoneId) {
		this.file = f;
		this.nodeList = nodes;
		this.phoneId = phoneId;
	}
	
	public VStoreFile getVStoreFile() {
		return file;
	}
	
	public List<NodeInfo> getNodes() {
		return nodeList;
	}
	
	public String getPhoneId() {
		return this.phoneId;
	}

}
