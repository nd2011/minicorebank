-- customers
create table if not exists customers (
  id bigint primary key auto_increment,
  full_name varchar(200) not null,
  phone varchar(30),
  email varchar(120),
  status varchar(20) not null,
  created_at timestamp not null default current_timestamp
);

-- accounts
create table if not exists accounts (
  id bigint primary key auto_increment,
  customer_id bigint not null,
  account_no varchar(30) not null unique,
  type varchar(20) not null,
  status varchar(20) not null,
  currency varchar(10) not null,
  balance_snapshot decimal(19,2) not null default 0,
  version bigint not null default 0,
  created_at timestamp not null default current_timestamp,
  constraint fk_accounts_customer foreign key (customer_id) references customers(id)
);

-- transactions
create table if not exists transactions (
  id bigint primary key auto_increment,
  tx_ref varchar(50) not null unique,
  type varchar(20) not null,
  status varchar(20) not null,
  amount decimal(19,2) not null,
  currency varchar(10) not null,
  from_account_id bigint null,
  to_account_id bigint null,
  idempotency_key varchar(80) null unique,
  note varchar(255),
  created_at timestamp not null default current_timestamp,
  index idx_tx_from (from_account_id),
  index idx_tx_to (to_account_id),
  constraint fk_tx_from foreign key (from_account_id) references accounts(id),
  constraint fk_tx_to foreign key (to_account_id) references accounts(id)
);

-- ledger_entries
create table if not exists ledger_entries (
  id bigint primary key auto_increment,
  transaction_id bigint not null,
  account_id bigint not null,
  direction varchar(10) not null,
  amount decimal(19,2) not null,
  currency varchar(10) not null,
  created_at timestamp not null default current_timestamp,
  index idx_ledger_account (account_id),
  constraint fk_ledger_tx foreign key (transaction_id) references transactions(id),
  constraint fk_ledger_account foreign key (account_id) references accounts(id)
);

-- users (login)
create table if not exists users (
  id bigint primary key auto_increment,
  username varchar(50) not null unique,
  password_hash varchar(255) not null,
  role varchar(20) not null,
  customer_id bigint null,
  created_at timestamp not null default current_timestamp,
  constraint fk_users_customer foreign key (customer_id) references customers(id)
);
