create table AUDIO_BLOCKS (
	ID IDENTITY PRIMARY KEY,
	START_TIME TIMESTAMP NOT NULL,
	END_TIME TIMESTAMP NOT NULL,
);

create index AUDIO_BLOCKS_N1 on AUDIO_BLOCKS(START_TIME);

