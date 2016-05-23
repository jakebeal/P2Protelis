package com.bbn.protelis.processmanagement.daemon;

import org.protelis.lang.datatype.Tuple;

import com.bbn.protelis.processmanagement.testbed.client.Message;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Monitorable {
	static public interface Listener {
		public void identifyNeighbor(Monitorable monitorable, InetAddress nbr, int port);
	}
	
	public abstract ProcessStatus getStatus();
	// Tuple of [[InetAddress, port] ...]
	public abstract Tuple knownDependencies() throws NumberFormatException, UnknownHostException;
	// TODO: determine whether dependenciesForProtelis() and dependenciesForDisplay() should exist
	//public abstract Tuple dependenciesForProtelis();
	//public abstract Tuple dependenciesForDisplay();
	public abstract int getCommPort();
	public abstract boolean init(); // signal to (re)initialize, returns success
	public abstract boolean shutdown(); // signal for graceful shutdown, returns success
	public abstract boolean crash(); // signal to crash, returns success

	private boolean recording = false;
	protected Deque<Message> record = new ConcurrentLinkedDeque<Message>();
	public boolean isRecording() { return recording; }
	public void setRecording(boolean r) { recording = r; }
	public Deque<Message> getRecord() { return record; }
	public Tuple getRecordAsTuple() { 
		ArrayList<Object> l = new ArrayList<>();
		for(Message m : record) { l.add(m); }
		return Tuple.create(l);
	}

	private Set<Listener> listeners = new HashSet<>();
	public void addListener(Listener l) { listeners.add(l); }
	public void deleteListener(Listener l) { listeners.remove(l); }
	public void identifyNeighbor(InetAddress nbr, int port) {
		listeners.forEach((l) -> {
			l.identifyNeighbor(this,nbr,port);
		});
	}
	public void recordInteraction(InetAddress nbr, int port, Message packet) {
		if(port>0) identifyNeighbor(nbr,port);
		if(recording) record.add(packet);
	}
}

