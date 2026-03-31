## Gui Notes
- Load filler items first, which are used for things like border patterns.
  - Internally, there is a check that prevents filling slots via Paginated/SimpleGui methods if they are already present.
- Load items added through methods in Paginated/SimpleGui
- Open the inventory, which does all the magic.
- Profit

If the inventory needs updating, there is `updatePageItem`, `removePageItem`, and other methods not related to paginating.