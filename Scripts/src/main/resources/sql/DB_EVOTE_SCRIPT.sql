DROP TABLE IF EXISTS `code`;
DROP TABLE IF EXISTS `utilisateur`;

-- Create tables and other statements

CREATE TABLE `utilisateur`
(
    `login`            varchar(40)   NOT NULL,
    `password`         varchar(1000) NOT NULL,
    `vote`             tinyint(1)   DEFAULT 0,
    `mail`             varchar(100) DEFAULT NULL,
    `essaiRestant`     int(11)      DEFAULT 3,
    `currentTimeStamp` datetime     DEFAULT NULL,
    PRIMARY KEY (`login`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `code`;
CREATE TABLE `code`
(
    `code`             varchar(1000) NOT NULL,
    `utilisateur`      varchar(40)   NOT NULL,
    `currentTimeStamp` datetime      NOT NULL,
    KEY `foreign_key_name` (`utilisateur`),
    CONSTRAINT `foreign_key_name` FOREIGN KEY (`utilisateur`) REFERENCES `utilisateur` (`login`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


--
-- Table structure for table `essaiConnexion`
--

DROP TABLE IF EXISTS `essaiConnexion`;
CREATE TABLE `essaiConnexion`
(
    `timer`       datetime DEFAULT NULL,
    `nbEssai`     int(11)  DEFAULT NULL,
    `utilisateur` int(11)  DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO evote.utilisateur (login, password, vote, mail, essaiRestant, currentTimeStamp)
VALUES ('quentinb', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 0, null, 3, null);
INSERT INTO evote.utilisateur (login, password, vote, mail, essaiRestant, currentTimeStamp)
VALUES ('axelf', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 0, null, 3, null);
INSERT INTO evote.utilisateur (login, password, vote, mail, essaiRestant, currentTimeStamp)
VALUES ('damienm', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 0, 'damien.trot34@gmail.com', 3, null);
INSERT INTO evote.utilisateur (login, password, vote, mail, essaiRestant, currentTimeStamp)
VALUES ('gaetang', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 0, null, 3, null);
INSERT INTO evote.utilisateur (login, password, vote, mail, essaiRestant, currentTimeStamp)
VALUES ('dimitric', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 0, 'dimitri2copley@gmail.com', 3, null);
INSERT INTO evote.utilisateur (login, password, vote, mail, essaiRestant, currentTimeStamp)
VALUES ('admin', 'admin', 0, null, 3, null);


-- ...
-- Other parts of your SQL script

DROP EVENT IF EXISTS debloquageUtilisateur;
CREATE EVENT debloquageUtilisateur ON SCHEDULE
    EVERY '1' MINUTE
        STARTS '2022-12-19 10:36:00'
    ENABLE
    DO
    UPDATE utilisateur
    SET essaiRestant    = 3,
        currentTimeStamp=NULL
    WHERE currentTimeStamp < CURRENT_TIMESTAMP + INTERVAL - 5 MINUTE;

DROP EVENT IF EXISTS timeDelete;
CREATE EVENT timeDelete ON SCHEDULE
    EVERY '1' MINUTE
        STARTS '2022-11-20 19:57:07'
    ENABLE
    DO
    DELETE
    FROM code
    WHERE currentTimeStamp < CURRENT_TIMESTAMP + INTERVAL - 15 MINUTE;
