TOKEN=TUlOSFRBMTZAYXVndXN0YW5hLmVkdQ==:eyJhbGciOiJIUzUxMiJ9.eyJ1c2VyX2lkIjoxNzUzMzI2LCJnZW5lcmF0ZWRfYXQiOiIyMDE5LTAzLTA2IDE5OjEzOjE5In0.DxvUav8KRxixHbAAMZw5n6Kq19mzOJCc58h2cd1uViFqELmhZ2aj7shKuqR-K6Z58K6BsCLdmP4-XpETCtksfg
#curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+json' -H 'Content-type: application/json' -X GET https://api.samanage.com/incidents.json | python -mjson.tool > "formatted.json

# Get incidents ------------------------------------------------
curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-type: application/xml' -X GET "https://api.samanage.com/incidents.xml?per_page=100&page=1&created%5B%5D=Select%20Date%20Range&created_custom_gte%5B%5D=01/04/2019&created_custom_lte%5B%5D=26/04/2019&layout=long" -o incidents_time.xml


# Get categories ----------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET "https://api.samanage.com/categories.xml" -o categories.xml

# Get users ---------------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -X GET "https://api.samanage.com/users.xml" -o users.xml

# Get departments ---------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -X GET https://api.samanage.com/departments.xml -o department.xml

# Get sites ---------------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -X GET "https://api.samanage.com/sites.xml?name=Andreen" -o sites.xml

# New incident ------------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -d '
# <incident>
#  <name>Test</name>
#  <priority>Medium</priority>
#  <requester><email>MINHTA16@augustana.edu</email></requester>
#   <category><name>Meetings  (ITS use only)</name></category>
#   <subcategory>
#        <name>Planning/Consulting/Brainstorming</name>
#   </subcategory>
#  <description>&lt;a href="mailto:minhta16@augustana.edu"&gt;performingarts@augustana.edu&lt;/a&gt;minhta16@augustana.edu&lt;/a&gt;</description>
#  <due_at>Mar 20, 2019</due_at>
# </incident>
# ' -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X POST https://api.samanage.com/incidents.xml

# Edit incident -------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -d '
# <incident>
#     <assignee><email>minhta16@augustana.edu</email></assignee>
# </incident>
# ' -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X PUT https://api.samanage.com/incidents/36935362.xml

# Get softwares -------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -X GET "https://api.samanage.com/softwares.xml" -o softwares.xml

# New time track -----------------------------------------------
ID=36681945
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -d '
# <time_track>
#   <name>slow</name>
#   <creator_id>1753326</creator_id>
#   <minutes_parsed>25 minutes</minutes_parsed>
# </time_track>
# ' -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X POST https://api.samanage.com/incidents/$ID/time_tracks.xml

# Get time track ----------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET https://api.samanage.com/incidents/$ID/time_tracks.xml -o testtimetrack.xml

# Edit time track --------------------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -d '
# <time_track>
#     <created_at>2019-03-24T08:00:00Z</created_at>
# </time_track>
# ' -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X PUT "https://api.samanage.com/incidents/$ID/time_tracks/2850248.xml"

# Get groups -----------------------------------
# curl -H "X-Samanage-Authorization: Bearer $TOKEN" -H 'Accept: application/vnd.samanage.v2.1+xml' -H 'Content-Type:text/xml' -X GET https://api.samanage.com/groups.xml -o groups.xml
