import os
import logging



# loogging setup 
logger = logging.getLogger(__name__)
#================================================================================================================== Validate audio files
def validate_audio_files(audio_files):

    try:
        if not audio_files:
            print("PROCESS_AUDIO.PY: No audio files found.")
            return

        
        upload_dir = os.path.dirname(audio_files[0])
        print(f"PROCESS_AUDIO.PY: Upload directory: {upload_dir}")

        audio_exists_count = 0
        
        for i, audio_file in enumerate(audio_files):
            print(f"PROCESS_AUDIO.PY: Processing audio file in for loop: {audio_file}")
            # Validate upload directory
            if os.path.exists(audio_file):
                audio_exists_count += 1
                logger.info(f"PROCESS_AUDIO.PY: Audio file exists and will be processed: {audio_file}")
                print(f"{'='*80}\nPROCESS_AUDIO.PY: File name - {audio_file}")
            else:
                logger.warning(f"PROCESS_AUDIO.PY: Audio file does NOT exist: {audio_file}")
        
        print(f"PROCESS_AUDIO.PY: Total existing files: {audio_exists_count}/{len(audio_files)}")

    except Exception as e:
        print(f"PROCESS_AUDIO.PY: Error during file and directory validation: {str(e)}")

