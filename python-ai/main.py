from utils.logger import get_logger
from config.path_config import UPLOAD_DIR
from src.file_handler import extract_audio_paths
from utils.custom_exception import CustomException
from ai.process_audio import process_recording_data

logger = get_logger(__name__)

def main():
    try:
        logger.info("="*50 + "\n")
        logger.info("Starting the audio path extraction process.")
        audio_paths = extract_audio_paths(UPLOAD_DIR)
        logger.info(f"Audio paths extracted successfully: {audio_paths}")
        
        # Assuming the recording file path is provided or set to an empty string
        process_recording_data(recording_file_path="", audio_files=audio_paths)
        logger.info("Audio processing completed successfully.")
        logger.info("="*50 + "\n")

    except CustomException as e:
        logger.error(f"CustomException occurred: {e}")
    except Exception as e:
        logger.error(f"An unexpected error occurred: {e}")

if __name__ == "__main__":
    main()
