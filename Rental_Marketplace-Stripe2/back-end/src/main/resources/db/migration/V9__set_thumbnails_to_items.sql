UPDATE item
SET thumbnail = CASE
    WHEN id = 1 THEN 'https://www.apple.com/newsroom/images/product/mac/standard/Apple-MacBook-Pro-M2-Pro-and-M2-Max-hero-230117_Full-Bleed-Image.jpg.medium_2x.jpg'
    WHEN id = 2 THEN 'https://www.apple.com/newsroom/images/2023/11/apple-supercharges-logic-pro-for-mac-and-ipad/article/Apple-Logic-Pro-music-creation_big.jpg.medium_2x.jpg'
    WHEN id = 4 THEN 'https://www.apple.com/newsroom/images/2023/11/apple-supercharges-logic-pro-for-mac-and-ipad/article/Apple-Logic-Pro-music-creation_big.jpg.medium_2x.jpg'
    ELSE NULL
END;