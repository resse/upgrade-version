#!/usr/bin/env python

import sqlite3

conn = sqlite3.connect('/var/lib/pgadmin/pgadmin4.db')
data = [1,1,1,'postgres','postgres',5432,'postgres','postgres','Zig6sg4kaRBcNib+eZelLQRh47PlesY=','prefer']
query = 'insert into server (id, user_id, servergroup_id, name, host, port, maintenance_db, username, password, ssl_mode) values (?,?,?,?,?,?,?,?,?,?)'
conn.cursor().execute(query, data)
conn.commit()