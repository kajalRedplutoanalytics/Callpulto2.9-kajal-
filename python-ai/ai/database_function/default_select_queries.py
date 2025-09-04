"""
Default select queries for the database.
These queries are used to fetch data from various tables in the capital market schema.
"""
import os
import configparser



# Load config
config = configparser.ConfigParser()
config.read('config.ini')
db_conf = config['DATABASE']

schema_name = db_conf.get('schema')

tables = {
    'trading_order_details': db_conf.get('trading_order_details_table'),
    'equity_details': db_conf.get('equity_details_table'),
    'employee_details': db_conf.get('employee_details_table'),
    'client_details': db_conf.get('client_details_table'),
    'recording_details': db_conf.get('recording_details_table'),
    'call_order_details': db_conf.get('call_order_details_table'),
    'mismatch_threshold': db_conf.get('mismatch_threshold_table'),
    'mismatch_reasons': db_conf.get('mismatch_reasons_table'),
    'product_details': db_conf.get('product_details_table')
}

trading_order_select_query = f"""
        select tod.trading_order_details_id,tod.client_id, tod.rm_id, concat(ed.first_name,' ',ed.last_name) as rm_name,
        concat(cd.first_name,' ',cd.last_name) as client_name, 
        LOWER(TRIM(eqd.instrument_name)) AS trading_order_instrument_name,
        tod.quantity as trading_order_quantity, tod.price as trading_order_price,
        tod.order_execution_time, rd.call_type,tod.transaction_type, tod.stop_loss, tod.order_Status, tod.target_price, 
        eqd.product_details_id ,mt.price_threshold,mt.quantity_threshold, mt.time_threshold
	from {schema_name}.{tables['trading_order_details']} tod
	left join  {schema_name}.{tables['recording_details']} rd
	    on tod.trading_order_details_id=rd.trading_order_details_id
	left outer join {schema_name}.{tables['equity_details']} eqd
	    on tod.instrument_id=eqd.instrument_id
	left  outer join {schema_name}.{tables['employee_details']} ed
	    on tod.rm_id=ed.rm_id
	left outer join {schema_name}.{tables['client_details']} cd
	    on cd.client_id=tod.client_id
    left outer join {schema_name}.{tables['mismatch_threshold']}  mt
	on mt.product_details_id=tod.product_details_id
	where rd.recording_flag is null;
    """

call_order_select_query = f"""
        select cod.call_order_details_id,rd.client_id,rd.rm_id, concat(empd.first_name,' ',empd.last_name) as rm_name,
            concat(cd.first_name,' ',cd.last_name) as client_name,
            LOWER(TRIM(cod.extracted_instrument_name)) AS call_order_instrument_name
            ,cod.quantity as call_order_quantity, cod.price as call_order_price,cod.order_placed_time, 
            rd.call_type,cod.transaction_type, cod.stop_loss, cod.order_Status, cod.target_price
            , null as product_details_id 
        from {schema_name}.{tables['call_order_details']}  cod 
        inner join (select distinct recording_name,client_id,rm_id,call_type from {schema_name}.{tables['recording_details']}  rd 
            where rd.recording_flag is null) rd
            on cod.recording_name=rd.recording_name
        left  outer join  {schema_name}.{tables['employee_details']} empd
            on rd.rm_id=empd.rm_id
        left outer join {schema_name}.{tables['client_details']}  cd
            on rd.client_id=cd.client_id
        where cod.transaction_type in ('Buy', 'Sell');
    """

equity_details_select_query = f"""
            SELECT 
                eq.instrument_id, 
                LOWER(TRIM(eq.instrument_name)) AS instrument_name, 
                LOWER(TRIM(eq.nse_code)) AS nse_code,
                LOWER(TRIM(eq.bse_code)) AS bse_code
            FROM 
                {schema_name}.{tables['equity_details']} eq;
            """


mismatch_reason_select_query = f"""
    SELECT mismatch_reason_id, category, reason 
    FROM {schema_name}.{tables['mismatch_reasons']}
    WHERE is_active = 'Y';"""

recording_details_select_query = f"""
        SELECT client_id,rm_id,recording_details_id, recording_id, recording_name,  
        recording_flag, start_time, end_time 
        FROM {schema_name}.{tables['recording_details']};
    """

product_details_select_query=f"""
        SELECT product_details_id, product_name FROM {schema_name}.{tables['product_details']}
        """