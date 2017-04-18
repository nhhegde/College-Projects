--  Neil Hegde (c) 2012
DROP VIEW IF EXISTS q1a, q1b, q1c, q1d, q2, q3, q4, q5, q6, q7;

-- Question 1a
CREATE VIEW q1a(id, amount)
AS
  SELECT cmte_id, transaction_amt
  FROM committee_contributions
  WHERE transaction_amt > 5000 
;

-- Question 1b
CREATE VIEW q1b(id, name, amount)
AS
  SELECT cmte_id, name, transaction_amt 
  FROM committee_contributions 
  WHERE transaction_amt > 5000
;

-- Question 1c
CREATE VIEW q1c(id, name, avg_amount)
AS
  
  SELECT cmte_id, name, avg(transaction_amt)
  FROM committee_contributions 
  WHERE transaction_amt > 5000
  GROUP BY name, cmte_id
;

-- Question 1d
CREATE VIEW q1d(id, name, avg_amount)
AS
  SELECT id, name, avg_amount
  FROM q1c
  WHERE avg_amount > 10000
;



-- Question 2

CREATE VIEW q2(from_name, to_name)
AS
  WITH t2 AS (
  SELECT committees.name, intercommittee_transactions.other_id
  FROM intercommittee_transactions
  INNER JOIN committees
  ON intercommittee_transactions.cmte_id=committees.id
  WHERE committees.pty_affiliation='DEM'
  )
  SELECT committees.name, t2.name
  FROM t2
  INNER JOIN committees
  ON t2.other_id=committees.id
  WHERE committees.pty_affiliation='DEM'
  GROUP BY committees.name, t2.name
  ORDER BY COUNT(*) DESC
  LIMIT 10
;


--  
-- Question 3
CREATE VIEW q3(name)
AS
 SELECT committees.name
  FROM committees

  EXCEPT ALL
  SELECT committees.name 
  FROM committees
  Inner JOIN committee_contributions
  ON committee_contributions.cmte_id=committees.id
  WHERE committee_contributions.cand_id IS NOT NULL
  AND committee_contributions.cand_id = (SELECT candidates.id FROM candidates WHERE candidates.name = 'OBAMA, BARACK')
 ;

-- Question 4.
CREATE VIEW q4 (name)
AS
  WITH cmmt AS (
       SELECT committee_contributions.cand_id, committee_contributions.cmte_id
       FROM committee_contributions 
       WHERE committee_contributions.cand_id IS NOT NULL
       GROUP BY committee_contributions.cand_id, committee_contributions.cmte_id
    )

  SELECT candidates.name
  FROM candidates
  INNER JOIN (SELECT candidates.id
  FROM candidates, cmmt
  WHERE cmmt.cand_id=candidates.id 
  GROUP BY candidates.id
  HAVING COUNT(candidates.id) > (SELECT 0.01 * COUNT(1) FROM committees)) cc
  ON candidates.id = cc.id
;

-- Question 5
CREATE VIEW q5 (name, total_pac_donations) AS
  WITH amt AS (
    SELECT t.cmte_id, SUM(t.TRANSACTION_AMT)
    FROM (
    SELECT i.cmte_id, i.TRANSACTION_AMT
    FROM individual_contributions i
    WHERE i.ENTITY_TP='ORG') t
    GROUP BY t.cmte_id
    )

  SELECT c.name, amt.sum
  FROM committees c
  LEFT JOIN amt
  ON c.id = amt.cmte_id
  ORDER BY c.name
;

-- Question 6
CREATE VIEW q6 (id) AS
  WITH paccers AS (
    SELECT c.cand_id
    FROM committee_contributions c
    WHERE c.cand_id IS NOT NULL
    AND c.ENTITY_TP = 'PAC'
    GROUP BY c.cand_id
    )
  SELECT c2.cand_id
  FROM committee_contributions c2
  JOIN paccers
  ON c2.cand_id = paccers.cand_id
  WHERE c2.cand_id IS NOT NULL
  AND c2.ENTITY_TP = 'CCM'
  GROUP BY c2.cand_id

;

-- Question 7
CREATE VIEW q7 (cand_name1, cand_name2) AS
  WITH can AS (
  SELECT candidates.name, cc.cmte_id
  FROM committee_contributions cc
  JOIN candidates
  ON cc.cand_id = candidates.id
  WHERE cc.state = 'RI'
  AND cc.cand_id IS NOT NULL)

  SELECT DISTINCT c1.name AS cand_name1, c2.name AS cand_name2
  FROM can c1, can c2
  WHERE c1.cmte_id = c2.cmte_id
  AND c1.name != c2.name
;
