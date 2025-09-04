from config.path_config import LOG_DIR
from datetime import datetime
import logging
import sys
import os

# Configure logging
os.makedirs(LOG_DIR, exist_ok=True)
LOG_FILE = os.path.join(LOG_DIR, f"{datetime.now().strftime('%Y-%m-%d')}.log")

# Set up basic configuration for logging
logging.basicConfig(
    filename=LOG_FILE,
    format='[%(asctime)s] [lineno-%(lineno)d] [File_Name-%(name)s] - %(levelname)s - %(message)s',
    level=logging.INFO,
)

def get_logger(name):
    """
    Create and return a logger with the specified name.

    Args:
        name (_type_): The name of the logger.

    Returns:
        _type_: The configured logger.
    """
    logger = logging.getLogger(name=name)
    logger.setLevel(logging.INFO)
    
    return logger