TRUNCATE TABLE Closed_Request, Service_Request, Owns, Car, Mechanic, Customer RESTART IDENTITY CASCADE;

\copy Customer (id, fname, lname, phone, address) FROM 'data/customer.csv' WITH (FORMAT csv)
\copy Mechanic (id, fname, lname, experience) FROM 'data/mechanic.csv' WITH (FORMAT csv)
\copy Car (vin, make, model, year) FROM 'data/car.csv' WITH (FORMAT csv)
\copy Owns (ownership_id, customer_id, car_vin) FROM 'data/owns.csv' WITH (FORMAT csv)

CREATE TEMP TABLE tmp_service_request (
    rid INTEGER,
    customer_id INTEGER,
    car_vin VARCHAR(16),
    date_text TEXT,
    odometer INTEGER,
    complain TEXT
);

\copy tmp_service_request (rid, customer_id, car_vin, date_text, odometer, complain) FROM 'data/service_request.csv' WITH (FORMAT csv)

INSERT INTO Service_Request (rid, customer_id, car_vin, date, odometer, complain)
SELECT rid,
       customer_id,
       car_vin,
       TO_DATE(split_part(date_text, ' ', 1), 'MM/DD/YYYY'),
       odometer,
       complain
FROM tmp_service_request;

DROP TABLE tmp_service_request;

CREATE TEMP TABLE tmp_closed_request (
    wid INTEGER,
    rid INTEGER,
    mid INTEGER,
    date_text TEXT,
    comment TEXT,
    bill INTEGER
);

\copy tmp_closed_request (wid, rid, mid, date_text, comment, bill) FROM 'data/closed_request.csv' WITH (FORMAT csv)

INSERT INTO Closed_Request (wid, rid, mid, date, comment, bill)
SELECT wid,
       rid,
       mid,
       TO_DATE(split_part(date_text, ' ', 1), 'MM/DD/YYYY'),
       comment,
       bill
FROM tmp_closed_request;

DROP TABLE tmp_closed_request;

SELECT setval('customer_id_seq', COALESCE((SELECT MAX(id) FROM Customer), 0) + 1, false);
SELECT setval('mechanic_id_seq', COALESCE((SELECT MAX(id) FROM Mechanic), 0) + 1, false);
SELECT setval('owns_ownership_id_seq', COALESCE((SELECT MAX(ownership_id) FROM Owns), 0) + 1, false);
SELECT setval('service_request_rid_seq', COALESCE((SELECT MAX(rid) FROM Service_Request), 0) + 1, false);
SELECT setval('closed_request_wid_seq', COALESCE((SELECT MAX(wid) FROM Closed_Request), 0) + 1, false);
