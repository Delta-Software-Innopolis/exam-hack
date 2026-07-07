BEGIN;

WITH inserted_user AS (
    INSERT INTO users (name, pass_hash)
    VALUES ('main_user', 'mock_hash_123')
    RETURNING id
),
inserted_packs AS (
    INSERT INTO packs (name, creation_date, updating_date, share_code, author_id)
    VALUES
        ('Linear Algebra Basics', NOW(), NULL, 'pack_linear_algebra_basics', (SELECT id FROM inserted_user)),
        ('World History Essentials', NOW(), NULL, 'pack_world_history_essentials', (SELECT id FROM inserted_user)),
        ('Python Fundamentals', NOW(), NULL, 'pack_python_fundamentals', (SELECT id FROM inserted_user))
    RETURNING id, name
),
inserted_published AS (
    INSERT INTO published_pack (id, user_id, rating, subject, university, professor, course_book, description)
    VALUES
        (
            (SELECT id FROM inserted_packs WHERE name = 'Linear Algebra Basics'),
            (SELECT id FROM inserted_user),
            4.8,
            'Mathematics',
            'Innopolis University',
            'A. Petrov',
            'Linear Algebra with Applications',
            'Introductory pack for vectors, matrices, and systems of linear equations.'
        ),
        (
            (SELECT id FROM inserted_packs WHERE name = 'World History Essentials'),
            (SELECT id FROM inserted_user),
            4.6,
            'History',
            'Moscow State University',
            'I. Sokolov',
            'A Brief History of the World',
            'A broad overview of major historical periods and events.'
        ),
        (
            (SELECT id FROM inserted_packs WHERE name = 'Python Fundamentals'),
            (SELECT id FROM inserted_user),
            4.9,
            'Computer Science',
            'ITMO University',
            'N. Ivanova',
            'Learning Python',
            'Foundational questions about Python syntax, data types, and control flow.'
        )
    RETURNING id
),
inserted_cards AS (
    INSERT INTO cards (question, pack_id, hint)
    VALUES
        ('What is the determinant of the identity matrix?', (SELECT id FROM inserted_packs WHERE name = 'Linear Algebra Basics'), 'Think about the identity matrix.'),
        ('Which vector space axiom guarantees closure under addition?', (SELECT id FROM inserted_packs WHERE name = 'Linear Algebra Basics'), 'It concerns adding two vectors.'),
        ('In which year did the French Revolution begin?', (SELECT id FROM inserted_packs WHERE name = 'World History Essentials'), 'The late 18th century.'),
        ('Which civilization built the Machu Picchu complex?', (SELECT id FROM inserted_packs WHERE name = 'World History Essentials'), 'South America, Andes.'),
        ('What does the len() function return for a list in Python?', (SELECT id FROM inserted_packs WHERE name = 'Python Fundamentals'), 'It gives the count of items.'),
        ('Which keyword is used to define a function in Python?', (SELECT id FROM inserted_packs WHERE name = 'Python Fundamentals'), 'It is followed by the function name.' )
    RETURNING id, question
)
INSERT INTO card_options (content, is_right, card_id)
VALUES
    ('1', TRUE, (SELECT id FROM inserted_cards WHERE question = 'What is the determinant of the identity matrix?')),
    ('0', FALSE, (SELECT id FROM inserted_cards WHERE question = 'What is the determinant of the identity matrix?')),
    ('-1', FALSE, (SELECT id FROM inserted_cards WHERE question = 'What is the determinant of the identity matrix?')),
    ('Undefined', FALSE, (SELECT id FROM inserted_cards WHERE question = 'What is the determinant of the identity matrix?')),

    ('Closed under addition', TRUE, (SELECT id FROM inserted_cards WHERE question = 'Which vector space axiom guarantees closure under addition?')),
    ('Commutativity of multiplication', FALSE, (SELECT id FROM inserted_cards WHERE question = 'Which vector space axiom guarantees closure under addition?')),
    ('Existence of a zero vector', FALSE, (SELECT id FROM inserted_cards WHERE question = 'Which vector space axiom guarantees closure under addition?')),
    ('Scalar distributivity only', FALSE, (SELECT id FROM inserted_cards WHERE question = 'Which vector space axiom guarantees closure under addition?')),

    ('1789', TRUE, (SELECT id FROM inserted_cards WHERE question = 'In which year did the French Revolution begin?')),
    ('1812', FALSE, (SELECT id FROM inserted_cards WHERE question = 'In which year did the French Revolution begin?')),
    ('1917', FALSE, (SELECT id FROM inserted_cards WHERE question = 'In which year did the French Revolution begin?')),
    ('1492', FALSE, (SELECT id FROM inserted_cards WHERE question = 'In which year did the French Revolution begin?')),

    ('The Incas', TRUE, (SELECT id FROM inserted_cards WHERE question = 'Which civilization built the Machu Picchu complex?')),
    ('The Aztecs', FALSE, (SELECT id FROM inserted_cards WHERE question = 'Which civilization built the Machu Picchu complex?')),
    ('The Mayans', FALSE, (SELECT id FROM inserted_cards WHERE question = 'Which civilization built the Machu Picchu complex?')),
    ('The Olmecs', FALSE, (SELECT id FROM inserted_cards WHERE question = 'Which civilization built the Machu Picchu complex?')),

    ('The number of items in the list', TRUE, (SELECT id FROM inserted_cards WHERE question = 'What does the len() function return for a list in Python?')),
    ('The last item in the list', FALSE, (SELECT id FROM inserted_cards WHERE question = 'What does the len() function return for a list in Python?')),
    ('The type of the list', FALSE, (SELECT id FROM inserted_cards WHERE question = 'What does the len() function return for a list in Python?')),
    ('The sum of all values', FALSE, (SELECT id FROM inserted_cards WHERE question = 'What does the len() function return for a list in Python?')),

    ('def', TRUE, (SELECT id FROM inserted_cards WHERE question = 'Which keyword is used to define a function in Python?')),
    ('func', FALSE, (SELECT id FROM inserted_cards WHERE question = 'Which keyword is used to define a function in Python?')),
    ('lambda', FALSE, (SELECT id FROM inserted_cards WHERE question = 'Which keyword is used to define a function in Python?')),
    ('class', FALSE, (SELECT id FROM inserted_cards WHERE question = 'Which keyword is used to define a function in Python?'));

COMMIT;
