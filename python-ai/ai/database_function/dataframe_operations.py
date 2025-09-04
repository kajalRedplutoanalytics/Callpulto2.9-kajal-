# from ai.database_function.tableconfig import schema_name 
import os
import numpy as np
import pandas as pd
import configparser
from utils.logger import get_logger

logger = get_logger(__name__)

def prepare_data_for_postgres_insert(raw_df, required_columns):
    """
    Filters the DataFrame for the required columns and converts it into a list of tuples.
    
    Args:
        raw_df (pd.DataFrame): The full DataFrame containing raw data.
        required_columns (list): List of column names to include for insertion.
    
    Returns:
        list: A list of tuples, each representing one row of data.
    """
    if not set(required_columns).issubset(raw_df.columns):
        missing = set(required_columns) - set(raw_df.columns)
        raise ValueError(f"The following required columns are missing from the DataFrame: {missing}")
    
    filtered_df = raw_df[required_columns]
    data = list(filtered_df.itertuples(index=False, name=None))
    return data


def get_recording_details(recording_name, df):
    """
    Given a recording_name and a DataFrame, return the recording_id, client_id, and rm_id
    for the matching recording.

    Args:
        recording_name (str): The name of the recording to look for.
        df (pd.DataFrame): The DataFrame that contains the data.

    Returns:
        tuple: (recording_id, client_id, rm_id)

    Raises:
        ValueError: If the recording_name is not found in the DataFrame.
    """
    if 'recording_name' not in df.columns:
        raise ValueError("DataFrame must contain a 'recording_name' column.")

    filtered = df[df['recording_name'] == recording_name]

    if filtered.empty:
        raise ValueError(f"No data found for recording_name: {recording_name}")
    
    # Get the first match
    row = filtered.iloc[0]

    try:
        return row['recording_id'], row['client_id'], row['rm_id']
    except KeyError as e:
        raise ValueError(f"Missing expected column: {e}")