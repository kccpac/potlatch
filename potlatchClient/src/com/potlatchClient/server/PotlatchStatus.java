package com.potlatchClient.server;

public class PotlatchStatus {

	public enum PotlatchState {
		READY, PROCESSING
	}

	private PotlatchState state;

	public PotlatchStatus(PotlatchState state) {
		super();
		this.state = state;
	}

	public PotlatchState getState() {
		return state;
	}

	public void setState(PotlatchState state) {
		this.state = state;
	}

}