TOKEN=TUlOSFRBMTZAYXVndXN0YW5hLmVkdQ==:eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxNzUzMzI2LCJnZW5lcmF0ZWRfYXQiOiIyMDE5LTAzLTA2IDE5OjEzOjE5In0.DxvUav8KRxixHbAAMZw5n6Kq19mzOJCc58h2cd1uViFqELmhZ2aj7shKuqR-K6Z58K6BsCLdmP4-XpETCtksfg

#curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+json' -H 'Content-type: application/json' -X GET https://api.samanage.com/incidents.json | python -mjson.tool > "formatted.json"

# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-type: application/xml' -X GET https://api.samanage.com/incidents.xml?per_page=100 -o data.xml

#curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET https://api.samanage.com/incidents/16602/time_tracks.xml

# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -d '
# <incident>
#  <name>Test</name>
#  <priority>Medium</priority>
#  <requester><email>minhta16@augustana.edu</email></requester>
#  <category><name>Meetings (ITS use only)</name></category>
#  <subcategory>
#       <name>Training/Workshops</name>
#  </subcategory>
#  <description>Test curl-ing new incidents/description>
#  <assignee><email>minhta16@augustana.edu</email></assignee>
# </incident>
# ' -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X POST https://api.samanage.com/incidents.xml

curl -H "X-Samanage-Authorization: Bearer $TOKEN" -d '
<incident>
 <name>Test</name>
 <priority>Medium</priority>
 <requester><email>minhta16@augustana.edu</email></requester>
 <description>Test curl-ing new incidents/description>
</incident>
' -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X POST https://api.samanage.com/incidents.xml

# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -d '
# <time_track>
#   <name>slow</name>
#   <creator_id>1753326</creator_id>
#   <minutes_parsed>25</minutes_parsed>
# </time_track>
# ' -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X POST https://api.samanage.com/incidents/35881048/time_tracks.xml

# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET https://api.samanage.com/incidents/35881048/time_tracks.xml -o testtimetrack.xml