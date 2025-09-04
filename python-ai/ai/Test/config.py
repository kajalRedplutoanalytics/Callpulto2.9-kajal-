import configparser

# Load config
config = configparser.ConfigParser()
config.read('config.ini')
db_conf = config['DATABASE']
DB_HOST =db_conf.get('host') #DATABASEconfig['host']
DB_NAME = db_conf.get('name') #config['name']
DB_USER = db_conf.get('user') #config['user']
DB_PORT = db_conf.get('port') #config['port']
DB_PASSWORD =   db_conf.get('password') #config['password']
"""
host = callpluto.postgres.database.azure.com
port = 5432
user = callpluto
password = Redpluto@123
Database configuration module
This file contains the database connection parameters that can be modified during deployment
"""
import os

# Database connection parameters - should be stored in environment variables in production
#DB_HOST = os.environ.get("DB_HOST", "localhost")
#DB_NAME = os.environ.get("DB_NAME", "capital_market")
#DB_USER = os.environ.get("DB_USER", "postgres")
#DB_PASSWORD = os.environ.get("DB_PASSWORD", "redpluto1234")
#DB_PORT = os.environ.get("DB_PORT", "5432")
GEMINI_API_KEY = os.environ.get("GEMINI_API_KEY", "AIzaSyDfHUd68FJFboC6x7gxwqnlwtbj8DEGleQ")

# JWT Configuration
JWT_SECRET_KEY = os.environ.get("JWT_SECRET_KEY", "dev-secret-key")  # Change in production
JWT_ACCESS_TOKEN_EXPIRES = int(os.environ.get("JWT_ACCESS_TOKEN_EXPIRES", "28800"))  # 8 hours in seconds

# File Upload Configuration
UPLOAD_FOLDER = os.environ.get("UPLOAD_FOLDER", "uploads")

# File complete Configuration
COMPLETE_FOLDER = os.environ.get("COMPLETE_FOLDER", "complete")