--
-- PostgreSQL database dump
--

-- Dumped from database version 15.1
-- Dumped by pg_dump version 15.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: artifact_category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.artifact_category (
    id character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    socmed_id character varying(255) NOT NULL
);


ALTER TABLE public.artifact_category OWNER TO postgres;

--
-- Name: artifact_category_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.artifact_category_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.artifact_category_id_seq OWNER TO postgres;

--
-- Name: artifact_category_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.artifact_category_id_seq OWNED BY public.artifact_category.id;


--
-- Name: case_identity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.case_identity (
    id character varying(255) NOT NULL,
    investigators_name character varying(255) NOT NULL,
    handled_case character varying(255) NOT NULL,
    case_description character varying(255) NOT NULL
);


ALTER TABLE public.case_identity OWNER TO postgres;

--
-- Name: social_media; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.social_media (
    id character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.social_media OWNER TO postgres;

--
-- Name: socmed_regex; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.socmed_regex (
    id character varying(255) NOT NULL,
    field character varying(255) NOT NULL,
    regex character varying(255) NOT NULL,
    artifact_category_id character varying(255)
);


ALTER TABLE public.socmed_regex OWNER TO postgres;

--
-- Data for Name: artifact_category; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.artifact_category (id, name, socmed_id) FROM stdin;
d89a7cc4-cf15-4769-a03b-ee2094a19292	User Information	2f19a939-8434-489c-9062-8598074eef83
1007aece-a218-44c7-b4af-900b0ae97867	Comment	2f19a939-8434-489c-9062-8598074eef83
890ff7bc-adae-44ff-963b-1570f587694a	Account	2f19a939-8434-489c-9062-8598074eef83
\.


--
-- Data for Name: case_identity; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.case_identity (id, investigators_name, handled_case, case_description) FROM stdin;
8f410329-7ef2-4127-b153-4466cc676af2	q	q	q
\.


--
-- Data for Name: social_media; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.social_media (id, name) FROM stdin;
2f19a939-8434-489c-9062-8598074eef83	Facebook
4284e96b-4e66-40da-bf0b-26b1b3090b65	Instagram
749776ba-1c34-4080-994a-c54a37714cdc	WhatsApp
8c319d48-fbb9-47cb-b995-e7f2ecb6fd09	Telegram
1033cf2d-2dfa-468e-aba5-2c184e217ef1	Twitter
\.


--
-- Data for Name: socmed_regex; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.socmed_regex (id, field, regex, artifact_category_id) FROM stdin;
0407b8a4-2f3c-479a-9519-a4dc94b5fd9d	WhatsApp User	[A-Za-z0-9]+://[A-Za-z0-9]+\\.whatsapp\\.com/[A-Za-z0-9]+	\N
cfddee28-bc98-4139-89be-a87b80699d6a	Alfanumerik	[A-Za-z0-9]+	\N
fd89ba23-baa9-4ec2-89ea-21a296a7b281	Username	\\b(?:https?:\\/\\/)?(?:www\\.)?facebook\\.com\\/([a-zA-Z0-9_\\.]+)\\b	d89a7cc4-cf15-4769-a03b-ee2094a19292
cd15e5d6-7de6-41cc-913a-9b8be357f4b1	alfanumerik	[A-Za-z0-9]+	1007aece-a218-44c7-b4af-900b0ae97867
0849e175-beb3-4e83-a4c4-5cc861cc81ad	facebook.com	<!--(.*?)-->	890ff7bc-adae-44ff-963b-1570f587694a
\.


--
-- Name: artifact_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.artifact_category_id_seq', 1, false);


--
-- Name: artifact_category artifact_category_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.artifact_category
    ADD CONSTRAINT artifact_category_pkey PRIMARY KEY (id);


--
-- Name: case_identity case_identity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.case_identity
    ADD CONSTRAINT case_identity_pkey PRIMARY KEY (id);


--
-- Name: social_media social_media_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.social_media
    ADD CONSTRAINT social_media_pkey PRIMARY KEY (id);


--
-- Name: socmed_regex socmed_regex_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.socmed_regex
    ADD CONSTRAINT socmed_regex_pkey PRIMARY KEY (id);


--
-- Name: artifact_category artifact_category_social_media_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.artifact_category
    ADD CONSTRAINT artifact_category_social_media_id_fk FOREIGN KEY (socmed_id) REFERENCES public.social_media(id);


--
-- Name: socmed_regex socmed_regex_artifact_category_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.socmed_regex
    ADD CONSTRAINT socmed_regex_artifact_category_id_fk FOREIGN KEY (artifact_category_id) REFERENCES public.artifact_category(id);


--
-- PostgreSQL database dump complete
--

