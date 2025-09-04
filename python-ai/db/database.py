
"""
Database connection module
Provides functions to connect to the database and initialize the schema
"""
import os
import psycopg2
from psycopg2.extras import DictCursor
import logging
import sys
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '../../../')))
#from db.config import DB_HOST, DB_NAME, DB_USER, DB_PASSWORD, DB_PORT
from psycopg2.extras import RealDictCursor

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

logger = logging.getLogger(__name__)

#-------------------------------------------------------------------
# Database connection function
#-------------------------------------------------------------------
def connect_to_database():
    """Connect to PostgreSQL database.
    Returns:
        connection: psycopg2 connection object if successful, None otherwise.
    """
    try:
        connection = psycopg2.connect(
            host=DB_HOST,
            port=DB_PORT,
            user=DB_USER,
            password=DB_PASSWORD,
            dbname=DB_NAME  # PostgreSQL uses dbname instead of database
        )
        
        cursor = connection.cursor()
        cursor.execute("SET search_path TO capital_market;")  # Set schema
        return connection
    except psycopg2.Error as e:
        print(f"Error connecting to database: {e}")
        return None
    
def get_db_connection():
    """Create and return a database connection"""
    try:
        conn = psycopg2.connect(
            host=DB_HOST,
            database=DB_NAME,
            user=DB_USER,
            password=DB_PASSWORD,
            port=DB_PORT
        )
        return conn
    except Exception as e:
        logger.error(f"Database connection error: {e}")
        raise

#-------------------------------------------------------------------
def init_db():
    """Initialize the database schema if it doesn't exist"""
    conn = get_db_connection()
    cursor = conn.cursor()

    try:
        # Create schema if it doesn't exist
        cursor.execute("CREATE SCHEMA IF NOT EXISTS capital_market")
        conn.commit()

        # Execute SQL from schema file
        with open("sql/schema/users.sql", "r") as f:
            sql_script = f.read()
            cursor.execute(sql_script)

        conn.commit()
        logger.info("Database schema initialized successfully")

        # Insert initial test data if needed
        try:
            with open("sql/data/test_user.sql", "r") as f:
                sql_script = f.read()
                cursor.execute(sql_script)
            conn.commit()
            logger.info("Initial data loaded successfully")
        except Exception as e:
            conn.rollback()
            logger.warning(f"Could not load initial data: {e}")

    except Exception as e:
        conn.rollback()
        logger.error(f"Error initializing database: {e}")
        raise
    finally:
        cursor.close()
        conn.close()

#-------------------------------------------------------------------
def test_connection():
    """Test the database connection and return status"""
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT 1")
        result = cursor.fetchone()
        cursor.close()
        conn.close()
        return True, "Database connection successful"
    except Exception as e:
        return False, f"Database connection error: {e}"

#------------------------------------------------------------------- 
def get_audio_file_metadata(user_id, file_name):
    """
    Fetch metadata for a specific audio file from the database.
    """
    try:
        conn = get_db_connection()
        cursor = conn.cursor(cursor_factory=RealDictCursor)

        cursor.execute("""
            SELECT duration, transcript, flagged, last_accessed
            FROM capital_market.audio_files
            WHERE user_id = %s AND file_name = %s
        """, (user_id, file_name))

        result = cursor.fetchone()

        cursor.close()
        conn.close()

        return result or {}

    except Exception as e:
        print(f"DB error fetching audio metadata: {e}")
        return {}
    

