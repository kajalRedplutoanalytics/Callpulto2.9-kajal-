import os
from utils.logger import get_logger
from utils.custom_exception import CustomException
# from .utils import allowed_file, ALLOWED_EXTENSIONS_AUDIO

logger = get_logger(__name__)


def extract_audio_paths(upload_dir: str) -> list:
    audio_paths = []
    allowed_extensions = {'.ogg', '.mp3', '.m4a', '.wav'}

    try:
        if not os.path.exists(upload_dir):
            raise FileNotFoundError(f"Upload directory not found: {upload_dir}")

        for file in os.listdir(upload_dir):
            file_path = os.path.join(upload_dir, file)
            _, ext = os.path.splitext(file)
            if os.path.isfile(file_path) and ext.lower() in allowed_extensions:
                abs_path = os.path.abspath(file_path).replace("\\", "/")
                audio_paths.append(abs_path)

        logger.info(f"Found {len(audio_paths)} audio file(s) in '{upload_dir}'")
        return audio_paths

    except Exception as e:
        logger.error(f"Error while extracting audio paths: {e}")
        raise CustomException("Failed to extract audio paths", e)
