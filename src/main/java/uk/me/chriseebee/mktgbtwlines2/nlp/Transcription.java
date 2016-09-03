package uk.me.chriseebee.mktgbtwlines2.nlp;

import java.util.Date;

public class Transcription {


	private String transcriptionText;
	private Date   audioStartDate;
	
	public Transcription() {

	}

	public Transcription(String transcriptionText, Date audioStartDate) {
		super();
		this.transcriptionText = transcriptionText;
		this.audioStartDate = audioStartDate;
	}

	public String getTranscriptionText() {
		return transcriptionText;
	}
	public void setTranscriptionText(String transcriptionText) {
		this.transcriptionText = transcriptionText;
	}
	public Date getAudioStartDate() {
		return audioStartDate;
	}
	public void setAudioStartDate(Date audioStartDate) {
		this.audioStartDate = audioStartDate;
	}

	
}
