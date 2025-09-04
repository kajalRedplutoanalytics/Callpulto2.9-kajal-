import traceback
import sys

class CustomException(Exception):
    
    def __init__(self, error_message, error_detail: sys):
        """
        Custom exception class to handle exceptions with detailed error messages.

        Args:
            error_message (str): The error message to be displayed.
            error_detail (sys): The sys module to extract the stack trace.
        """
        super().__init__(error_message)
        self.error_message = self.get_detailed_error_message(error_message, error_detail)

#------------------------------------------------------------------------------------------------------- Get detailed error message

    @staticmethod
    def get_detailed_error_message(error_message, error_detail:sys):
        """
        Generates a detailed error message including the stack trace.

        Args:
            error_message (str): The error message to be displayed.
            error_detail (sys): The sys module to extract the stack trace.

        Returns:
            str: A detailed error message with the stack trace.
        """
        _,_, exc_tb = error_detail.exc_info()
        if exc_tb is not None:
            file_name = exc_tb.tb_frame.f_code.co_filename
            line_number = exc_tb.tb_lineno
        else:
            file_name = "Unknown"
            line_number = -1
        detailed_error_message = f"Error occurred in script: {file_name} at line number: {line_number} with error message: {error_message}"
        
        return detailed_error_message
        
    def __str__(self):
        return self.error_message