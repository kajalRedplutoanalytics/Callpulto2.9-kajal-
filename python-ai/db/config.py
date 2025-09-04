# Database connection parameters - should be stored in environment variables in production
import os


# Database Configuration
DB_HOST = os.environ.get("DB_HOST", "localhost")
DB_NAME = os.environ.get("DB_NAME", "capital_market")
DB_USER = os.environ.get("DB_USER", "postgres")
DB_PASSWORD = os.environ.get("DB_PASSWORD", "redpluto1234")
DB_PORT = os.environ.get("DB_PORT", "5432")
GEMINI_API_KEY = os.environ.get("GEMINI_API_KEY", "AIzaSyDfHUd68FJFboC6x7gxwqnlwtbj8DEGleQ")

# JWT Configuration
JWT_SECRET_KEY = os.environ.get("JWT_SECRET_KEY", "dev-secret-key")  # Change in production
JWT_ACCESS_TOKEN_EXPIRES = int(os.environ.get("JWT_ACCESS_TOKEN_EXPIRES", "28800"))  # 8 hours in seconds

# File Upload Configuration
UPLOAD_FOLDER = os.environ.get("UPLOAD_FOLDER", "uploads")

# File complete Configuration
COMPLETE_FOLDER = os.environ.get("COMPLETE_FOLDER", "complete")