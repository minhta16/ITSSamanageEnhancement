TOKEN=TUlOSFRBMTZAYXVndXN0YW5hLmVkdQ==:eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxNzUzMzI2LCJnZW5lcmF0ZWRfYXQiOiIyMDE5LTAzLTA2IDE5OjEzOjE5In0.DxvUav8KRxixHbAAMZw5n6Kq19mzOJCc58h2cd1uViFqELmhZ2aj7shKuqR-K6Z58K6BsCLdmP4-XpETCtksfg
#curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+json' -H 'Content-type: application/json' -X GET https://api.samanage.com/incidents.json | python -mjson.tool > "formatted.json

# Get incidents ------------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-type: application/xml' -X GET https://api.samanage.com/incidents.xml?per_page=1 -o data.xml

# Get categories ----------------------------------------------
curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET https://api.samanage.com/categories.xml -o categories.xml

# Get users ---------------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -X GET https://api.samanage.com/users.xml?per_page=1 -o user.xml

# New incident ------------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -d '
# <incident>
#  <name>Test</name>
#  <priority>Medium</priority>
#  <requester><email>MINHTA16@augustana.edu</email></requester>
#   <category><name>Meetings  (ITS use only)</name></category>
#   <subcategory>
#        <name>Training/Workshops</name>
#   </subcategory>
#  <description>Test curl-ing new incidents</description>
# </incident>
# ' -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X POST https://api.samanage.com/incidents.xml

# New time track -----------------------------------------------
ID=36010690
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -d '
# <time_track>
#   <name>slow</name>
#   <creator_id>1753326</creator_id>
#   <minutes_parsed>25</minutes_parsed>
# </time_track>
# ' -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X POST https://api.samanage.com/incidents/$ID/time_tracks.xml

# Get time track ----------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET https://api.samanage.com/incidents/$ID/time_tracks.xml -o testtimetrack.xml
