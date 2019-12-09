CREATE VIEW Transmission_Header as
SELECT o.state, o.patId, o.serotypesummary, o.dtspec,
      CASE WHEN serotypesummary != null THEN serotypeSummary
           ELSE (SELECT TOP 1 serotypeSummary
                   FROM oc_fdd_incidents99b i
                  WHERE o.state = i.state and o.patId = i.patId and i.serotypeSummary IS NOT NULL
                  ORDER BY ABS(DATEDIFF(d, o.dtSpec, i.dtSpect)))
      END AS CalcSeroTypeSummary

  FROM oc_incidents99b o