insert into USUARIOS (id, username, password, role)
    values (100, 'ana@gmail.com', '$2a$12$SXZsCnWtJOKCGSkb1cAfz.O3og3RxMCXaYDeVwrnspA279PmSwEM6', 'ROLE_ADMIN');
insert into USUARIOS (id, username, password, role)
    values (101, 'bia@gmail.com', '$2a$12$SXZsCnWtJOKCGSkb1cAfz.O3og3RxMCXaYDeVwrnspA279PmSwEM6', 'ROLE_CLIENTE');
insert into USUARIOS (id, username, password, role)
    values (102, 'bob@gmail.com', '$2a$12$SXZsCnWtJOKCGSkb1cAfz.O3og3RxMCXaYDeVwrnspA279PmSwEM6', 'ROLE_CLIENTE');

insert into CLIENTES (id, nome, cpf, id_usuario)
    values (21, 'Bianca Silva', '81170071007', 101);
insert into CLIENTES (id, nome, cpf, id_usuario)
    values (22, 'Roberto Gomes', '62352379016', 102);


insert into VAGAS (id, codigo, status)
    values (100, 'A-01', 'OCUPADA');
insert into VAGAS (id, codigo, status)
    values (200, 'A-02', 'OCUPADA');
insert into VAGAS (id, codigo, status)
    values (300, 'A-03', 'OCUPADA');
insert into VAGAS(id, codigo, status)
    values (400, 'A-04', 'LIVRE');
insert into VAGAS (id, codigo, status)
    values (500, 'A-05', 'LIVRE');

insert into clientes_tem_vagas(numero_recibo, placa, marca, modelo, cor, data_entrada, id_cliente, id_vaga)
    values ('20250313-101300', 'FIT-1020', 'FIAT', 'PALIO', 'VERDE', '2025-03-13 10:15:00', 22, 100);
insert into clientes_tem_vagas(numero_recibo, placa, marca, modelo, cor, data_entrada, id_cliente, id_vaga)
    values ('20250313-101400', 'SIE-1020', 'FIAT', 'SIENA', 'BRANCO', '2025-03-14 10:15:00', 21, 200);
insert into clientes_tem_vagas(numero_recibo, placa, marca, modelo, cor, data_entrada, id_cliente, id_vaga)
    values ('20250313-101500', 'FIT-1020', 'FIAT', 'PALIO', 'VERDE', '2025-03-14 10:15:00', 22, 300);
