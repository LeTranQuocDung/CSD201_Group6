const fs = require("fs");
const path = require("path");

const outputPath = path.join(__dirname, "mock_data.csv");
const stream = fs.createWriteStream(outputPath, { encoding: "utf8" });

const firstNames = [
  "An",
  "Binh",
  "Chi",
  "Dung",
  "Hanh",
  "Khanh",
  "Linh",
  "Minh",
  "Nam",
  "Phuong",
  "Quang",
  "Trang",
];

const lastNames = [
  "Nguyen",
  "Tran",
  "Le",
  "Pham",
  "Hoang",
  "Huynh",
  "Phan",
  "Vu",
  "Dang",
  "Bui",
];

function getRandomItem(items) {
  return items[Math.floor(Math.random() * items.length)];
}

stream.write("\ufeffID,Tên,Tuổi,Priority\n");

for (let i = 1; i <= 1000000; i++) {
  const name = `${getRandomItem(lastNames)} ${getRandomItem(firstNames)}`;
  const age = 18 + (i % 43);
  const priority = Math.floor(Math.random() * 4) + 1;

  stream.write(`${i},${name},${age},${priority}\n`);
}

stream.end(() => {
  console.log(`Created CSV file: ${outputPath}`);
});
