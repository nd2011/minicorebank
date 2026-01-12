insert into customers(full_name, phone, email, status)
select 'BANK HOUSE', null, null, 'ACTIVE'
where not exists (select 1 from customers where full_name = 'BANK HOUSE');

--seed house account
insert into accounts(customer_id, account_no, type, status, currency, balance_snapshot, version)
select c.id, 'HOUSE-000', 'HOUSE', 'ACTIVE', 'VND', 0, 0
from customers c
where c.full_name = 'BANK HOUSE'
    and not exists (select 1 from account where account_no = 'HOUSE-000');