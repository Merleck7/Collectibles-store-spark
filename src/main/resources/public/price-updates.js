const itemList = document.getElementById('itemList');
const socket = new WebSocket("ws://localhost:4567/price-updates");

socket.onmessage = (event) => {
  const data = JSON.parse(event.data);
  if (data.type === "update" && data.items) {
    renderItems(data.items);
  }
};

function renderItems(items) {
  itemList.innerHTML = items
    .map(item => `
      <div class="item">
        <h3>${item.name}</h3>
        <p>${item.description}</p>
        <span class="price">$ ${item.price.toFixed(2)}</span>
      </div>
    `)
    .join('');
}

document.getElementById("filterBtn").addEventListener("click", async () => {
  const name = document.getElementById("nameFilter").value;
  const min = document.getElementById("minPrice").value;
  const max = document.getElementById("maxPrice").value;

  const params = new URLSearchParams({ name, min, max });
  const res = await fetch(`/filter?${params}`);
  const filtered = await res.json();
  renderItems(filtered);
});
