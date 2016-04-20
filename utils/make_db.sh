#!/bin/bash

sudo -i -u postgres -p
psql
\c proxyprint
select * from users;

