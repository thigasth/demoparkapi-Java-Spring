insert into USUARIOS (id, username, password, role) values (100, 'ana@gmail.com', '$2a$12$SXZsCnWtJOKCGSkb1cAfz.O3og3RxMCXaYDeVwrnspA279PmSwEM6', 'ROLE_ADMIN');
insert into USUARIOS (id, username, password, role) values (101, 'bia@gmail.com', '$2a$12$SXZsCnWtJOKCGSkb1cAfz.O3og3RxMCXaYDeVwrnspA279PmSwEM6', 'ROLE_CLIENTE');
insert into USUARIOS (id, username, password, role) values (102, 'bob@gmail.com', '$2a$12$SXZsCnWtJOKCGSkb1cAfz.O3og3RxMCXaYDeVwrnspA279PmSwEM6', 'ROLE_CLIENTE');
insert into USUARIOS (id, username, password, role) values (103, 'toby@gmail.com', '$2a$12$SXZsCnWtJOKCGSkb1cAfz.O3og3RxMCXaYDeVwrnspA279PmSwEM6', 'ROLE_CLIENTE');

insert into CLIENTES (id, nome, cpf, id_usuario) values (10, 'Bianca Silva', '81170071007', 101);
insert into CLIENTES (id, nome, cpf, id_usuario) values (20, 'Roberto Gomes', '62352379016', 102);