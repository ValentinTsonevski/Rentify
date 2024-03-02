INSERT INTO item (name, description, price, category_id, user_id, posted_date, deposit, address_id)
VALUES
    ('MacBook 15', 'M3 Max: Full native resolution support on the built-in display at 1 billion colors. Up to four external displays - Up to three external displays with 6K resolution at 60Hz over Thunderbolt and one external display with up to 4K resolution at 144Hz over HDMI. Up to three external displays - Up to two external displays with 6K resolution at 60Hz over Thunderbolt and one external display with up to 8K resolution at 60Hz or one external display with 4K resolution at 240Hz over HDMI. Thunderbolt 4 digital video output. Support for native DisplayPort output over USB‑C. HDMI digital video output - Support for one display with up to 4K resolution at 120Hz (M3). Support for one display with up to 8K resolution at 60Hz or 4K resolution at 240Hz (M3 Pro and M3 Max).',
     3000.00, 3, 1, NOW(), 1000.00, 1);

INSERT INTO picture (url, item_id) VALUES
    ('https://www.apple.com/newsroom/images/product/mac/standard/Apple-MacBook-Pro-M2-Pro-and-M2-Max-hero-230117_Full-Bleed-Image.jpg.medium_2x.jpg', 1),
    ('https://www.apple.com/newsroom/images/product/mac/standard/Apple-MacBook-Pro-Cinema-4D-230117_big.jpg.medium_2x.jpg', 1),
    ('https://www.apple.com/newsroom/images/product/mac/standard/Apple-MacBook-Pro-M2-Pro-and-M2-Max-Stage-Manager-230117_big.jpg.medium_2x.jpg', 1),
    ('https://www.apple.com/newsroom/images/2023/11/apple-supercharges-logic-pro-for-mac-and-ipad/article/Apple-Logic-Pro-music-creation_big.jpg.medium_2x.jpg', 2),
    ('https://www.apple.com/newsroom/images/2023/11/apple-supercharges-logic-pro-for-mac-and-ipad/article/Apple-Logic-Pro-music-creation_big.jpg.medium_2x.jpg', 4);